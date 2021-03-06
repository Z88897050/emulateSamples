// Copy from zap

package qiniulog

import (
	"runtime"
	"strings"
	"sync"

	"go.uber.org/zap/buffer"
)

const _packagePrefix = "cases/qiniulog"

var (
	_pool = buffer.NewPool()
	// Get retrieves a buffer from the pool, creating one if necessary.
	Get = _pool.Get
)

var (
	_stacktracePool = sync.Pool{
		New: func() interface{} {
			return newProgramCounters(64)
		},
	}

	// We add "." and "/" suffixes to the package name to ensure we only match
	// the exact package and not any package with the same prefix.
	_stacktracePrefixes       = addPrefix(_packagePrefix, ".", "/")
	_stacktraceVendorContains = addPrefix("/vendor/", _stacktracePrefixes...)
)

func TakeStacktrace() string {
	buffer := Get()
	defer buffer.Free()
	programCounters := _stacktracePool.Get().(*programCounters)
	defer _stacktracePool.Put(programCounters)

	var numFrames int
	for {
		// Skip the call to runtime.Counters and takeStacktrace so that the
		// program counters start at the caller of takeStacktrace.
		numFrames = runtime.Callers(2, programCounters.pcs)
		if numFrames < len(programCounters.pcs) {
			break
		}
		// Don't put the too-short counter slice back into the pool; this lets
		// the pool adjust if we consistently take deep stacktraces.
		programCounters = newProgramCounters(len(programCounters.pcs) * 2)
	}

	i := 0
	skipZapFrames := true // skip all consecutive zap frames at the beginning.
	frames := runtime.CallersFrames(programCounters.pcs[:numFrames])

	// Note: On the last iteration, frames.Next() returns false, with a valid
	// frame, but we ignore this frame. The last frame is a a runtime frame which
	// adds noise, since it's only either runtime.main or runtime.goexit.
	for frame, more := frames.Next(); more; frame, more = frames.Next() {
		if skipZapFrames && isZapFrame(frame.Function) {
			continue
		} else {
			skipZapFrames = false
		}

		if i != 0 {
			buffer.AppendByte('\n')
		}
		i++
		buffer.AppendString(frame.Function)
		buffer.AppendByte('\n')
		buffer.AppendByte('\t')
		buffer.AppendString(frame.File)
		buffer.AppendByte(':')
		buffer.AppendInt(int64(frame.Line))
	}

	return buffer.String()
}

func isZapFrame(function string) bool {
	for _, prefix := range _stacktracePrefixes {
		if strings.HasPrefix(function, prefix) {
			return true
		}
	}

	// We can't use a prefix match here since the location of the vendor
	// directory affects the prefix. Instead we do a contains match.
	for _, contains := range _stacktraceVendorContains {
		if strings.Contains(function, contains) {
			return true
		}
	}

	return false
}

type programCounters struct {
	pcs []uintptr
}

func newProgramCounters(size int) *programCounters {
	return &programCounters{make([]uintptr, size)}
}

func addPrefix(prefix string, ss ...string) []string {
	withPrefix := make([]string, len(ss))
	for i, s := range ss {
		withPrefix[i] = prefix + s
	}
	return withPrefix
}

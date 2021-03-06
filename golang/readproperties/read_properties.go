package readproperties
// 可参考:github.com/magiconair/properties

import (
	"errors"
	"os"
	"runtime"
	"strconv"
	"strings"

	"github.com/magiconair/properties"
)

// Config is collect_file properties
type Config struct {
	// Prefix:  "collect.file.",
	// Postfix: "",
	Paths      string `properties:"collect.file.paths"`
	Mode       string `properties:"collect.file.mode,default=lazy"`
	Threads    string `properties:"collect.file.threads,default=0"`
	Interval   string `properties:"collect.file.interval,default=5"`
	Output     string `properties:"collect.file.output,default=console"`
	KafkaAddr  string `properties:"collect.file.kafka.address"`
	KafkaTopic string `properties:"collect.file.kafka.topic"`
}

// CollectCfg filling collect_file to map
func CollectCfg(path string) (*Config, error) {
	pStr := strings.Join([]string{path, "config", "collectfile.properties"},
		string(os.PathSeparator))
	cp, err := properties.LoadFile(pStr, properties.UTF8)
	if err != nil {
		return nil, err
	}
	var c Config
	if err := cp.Decode(&c); err != nil {
		return nil, err
	}
	if len(c.Paths) == 0 {
		return nil, errors.New("路径为空")
	}

	if len(c.Threads) == 0 {
		c.Threads = strconv.Itoa(runtime.NumCPU())
	}
	if len(c.Interval) == 0 {
		c.Interval = strconv.Itoa(5)
	}
	if len(c.Output) == 0 {
		c.Output = "console"
	}
	// strconv.ParseInt
	return &c, nil
}
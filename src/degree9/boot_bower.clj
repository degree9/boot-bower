(ns degree9.boot-bower
  {:boot/export-tasks true}
  (:require [boot.core :as boot]
            [boot.tmpdir :as tmpd]
            [boot.util :as util]
            [degree9.boot-exec :as exec]
            [clojure.java.io :as io]
            [boot.task.built-in :as tasks]
            [cheshire.core :refer :all]))

(boot/deftask bower
  "boot-clj wrapper for bower"
  [i install     FOO=BAR {kw str} "Dependency map."
   d directory   VAL     str      "Directory to install components (defaults to 'bower_components' in target)"
   g ignore      VAL     #{kw}    "List of packages to ignore."
   r resolutions VAL     {kw str} "Dependency resolutions."
   c cache-key   VAL     kw       "Optional cache key for when bower is used with multiple dependency sets."]
  (let [deps         (:install     *opts* "")
        dir          (:directory   *opts* "/bower_components")
        ignore       (:ignore      *opts* "")
        res          (:resolutions *opts* "")
        cache-key    (:cache-key   *opts*)
        tmp          (if cache-key (boot/cache-dir! cache-key) (boot/tmp-dir!))
        tmp-path     (.getAbsolutePath tmp)
        bwrjson      (generate-string {:name ::tmp :dependencies deps :resolutions res} {:pretty true})
        bwrrc        (generate-string {:directory dir :ignoredDependencies ignore} {:pretty true})]
    (comp
      (exec/properties :contents bwrjson :directory tmp-path :file "bower.json")
      (exec/properties :contents bwrrc :directory tmp-path :file ".bowerrc")
      (exec/exec :process "bower" :arguments ["install" "--allow-root"] :directory tmp-path :local "node_modules/bower/bin"))))

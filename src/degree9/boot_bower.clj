(ns degree9.boot-bower
  {:boot/export-tasks true}
  (:require [boot.core :as boot]
            [boot.tmpdir :as tmpd]
            [boot.util :as util]
            [clojure.java.io :as io]
            [clj-commons-exec :as exec]
            [boot.task.built-in :as tasks]
            [cheshire.core :refer :all]))

(boot/deftask bower
  "boot-clj wrapper for bower"
  [i install   FOO=BAR {kw str} "Dependency map."
   d directory VAL     str      "Directory to install components (defaults to 'bower_components' in target)"
   g ignore    VAL     #{kw}    "List of packages to ignore."]
  (let [deps    (or (:install   *opts*) "")
        dir     (or (:directory *opts*) "/bower_components")
        ignore  (or (:ignore    *opts*) "")
        tmp     (boot/tmp-dir!)
        tmpbwr  (boot/tmp-dir!)
        bwrjson (generate-string {:name ::tmp :dependencies deps} {:pretty true})
        bwrrc   (generate-string {:directory dir :ignoredDependencies ignore} {:pretty true})
        jsonf   (io/file tmp "bower.json")
        bwrrcf  (io/file tmp ".bowerrc")]
    (comp
     (boot/with-pre-wrap fileset
       (doto jsonf  io/make-parents (spit bwrjson))
       (doto bwrrcf io/make-parents (spit bwrrc))
       (let [local-bower (io/as-file "./node_modules/bower/bin/bower")
            bwrcmd      (if (.exists local-bower) (.getPath local-bower) "bower")
            cmdresult   @(exec/sh [bwrcmd "install" "--allow-root"] {:dir (.getPath tmp)})
            exitcode    (:exit cmdresult)
            errormsg    (:err cmdresult)]
         (assert (= 0 exitcode) errormsg)
         (util/info "Bower run successful...\n")
         (-> fileset (boot/add-resource tmp) boot/commit!)))
     )))

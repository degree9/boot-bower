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
  [i install     FOO=BAR {kw str} "Dependency map."
   d directory   VAL     str      "Directory to install components (defaults to 'bower_components' in target)"
   g ignore      VAL     #{kw}    "List of packages to ignore."
   r resolutions VAL     {kw str} "Dependency resolutions."]
  (let [deps         (or (:install     *opts*) "")
        dir          (or (:directory   *opts*) "/bower_components")
        ignore       (or (:ignore      *opts*) "")
        res          (or (:resolutions *opts*) "")
        tmp          (boot/tmp-dir!)
        tmpbwr       (boot/tmp-dir!)
        bwrjson      (generate-string {:name ::tmp :dependencies deps :resolutions res} {:pretty true})
        bwrrc        (generate-string {:directory dir :ignoredDependencies ignore} {:pretty true})
        jsonf        (io/file tmp "bower.json")
        bwrrcf       (io/file tmp ".bowerrc")
        local-bower  (io/as-file "./node_modules/bower/bin/bower")
        global-bower (io/as-file "/usr/local/bin/bower")
        bwrcmd       (cond (.exists global-bower) (.getPath global-bower)
                           (.exists local-bower) (.getAbsolutePath local-bower)
                           :else "bower")]
    (boot/with-pre-wrap fileset
       (doto jsonf  io/make-parents (spit bwrjson))
       (doto bwrrcf io/make-parents (spit bwrrc))
       (let [cmdresult   @(exec/sh [bwrcmd "install" "--allow-root"] {:dir (.getPath tmp)})
             exitcode    (:exit cmdresult)
             errormsg    (:err cmdresult)]
         (util/info (clojure.string/join ["Bower found at...: " bwrcmd "\n"]))
         (assert (= 0 exitcode) (util/fail (clojure.string/join ["Bower failed with...: \n" errormsg "\n"])))
         (util/info "Bower run successful...\n")
         (-> fileset (boot/add-resource tmp) boot/commit!)))))

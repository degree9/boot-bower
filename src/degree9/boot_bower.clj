(ns degree9.boot-bower
  (:require [boot.core :as boot]
            [boot.task.built-in :as tasks]
            [boot.tmpdir :as tmpd]
            [boot.util :as util]
            [cheshire.core :refer :all]
            [clojure.java.io :as io]
            [degree9.boot-exec :as exec]
            [degree9.boot-npm :as npm]))

(defn deep-merge
  "Like merge, but merges maps recursively."
  [& maps]
  (cond (every? map? maps) (apply merge-with deep-merge maps)
        (nil? (last maps)) (apply deep-merge (butlast maps))
        :else (last maps)))

(defn file->json [file]
  (let [file (boot/tmp-file file)]
    (parse-string (slurp file) true)))

(defn maps->str [& maps]
  (generate-string (apply deep-merge maps) {:pretty true}))

(defn spit-file [file path contents]
  (doto (io/file path file)
        io/make-parents
        (spit contents)))

(boot/deftask bower
  "boot-clj wrapper for bower"
  [b bower       VAL     str      "A bower.json file to parse."
   w bowerrc     VAL     str      "A .bowerrc file to parse."
   i install     FOO=BAR {kw str} "Dependency map."
   d directory   VAL     str      "Directory to install components (defaults to 'bower_components' in target)"
   g ignore      VAL     #{kw}    "List of packages to ignore."
   r resolutions VAL     {kw str} "Dependency resolutions."
   c cache-key   VAL     kw       "Optional cache key for when bower is used with multiple dependency sets."]
  (let [bwrjsonf  (:bower       *opts* "./bower.json")
        bwrrcf    (:bowerrc     *opts* "./.bowerrc")
        deps      (:install     *opts*)
        dir       (:directory   *opts* "bower_components")
        ignore    (:ignore      *opts*)
        res       (:resolutions *opts*)
        cache-key (:cache-key   *opts* ::cache)
        tmp       (boot/cache-dir! cache-key)
        tmp-path  (.getAbsolutePath tmp)
        bwrjson   {:name "temp" :dependencies deps :resolutions res}
        bwrrc     {:directory dir :ignoredDependencies ignore}
        args      ["install" "--allow-root"]
        args      (if (boot/get-env :offline?) (conj args "--offline") args)]
    (comp
      (boot/with-pass-thru fileset
        (let [bwrjsonfs (boot/tmp-get fileset bwrjsonf)
              bwrrcfs (boot/tmp-get fileset bwrrcf)]
          (if bwrjsonfs
            (spit-file (-> bwrjsonfs boot/tmp-file .getName) tmp (maps->str (file->json bwrjsonfs) bwrjson))
            (spit-file bwrjsonf tmp (maps->str bwrjson)))
          (if bwrrcfs
            (spit-file (-> bwrrcfs boot/tmp-file .getName) tmp (maps->str (file->json bwrrcfs) bwrrc))
            (spit-file bwrrcf tmp (maps->str bwrrc)))))
      (npm/exec :module "bower" :arguments args :cache-key cache-key))))

(ns degree9.boot-bower
  {:boot/export-tasks true}
  (:require [boot.core :as boot]
            [boot.tmpdir :as tmpd]
            [boot.util :as util]
            [clojure.java.io :as io]
            [me.raynes.conch :refer [programs with-programs let-programs] :as sh]))

(boot/deftask bower
  "boot-clj wrapper for bower"
  []
  (let [tmp         (boot/tmp-dir!)
        local-bower (io/as-file "./node_modules/bower/bin/bower")]
    (boot/with-pre-wrap fileset
      (let-programs [bwrcmd (if (.exists local-bower) (.getPath local-bower) "bower")]
                    (bwrcmd "install"))
        fileset)))

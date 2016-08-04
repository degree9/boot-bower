(set-env!
 :dependencies  '[[org.clojure/clojure                 "1.7.0"]
                  [boot/core                           "2.5.1"]
                  [adzerk/bootlaces                    "0.1.13"]
                  [cheshire                            "5.5.0"]
                  [degree9/boot-semver                 "1.2.0"]
                  [degree9/boot-exec                   "0.2.0"]]
 :resource-paths   #{"src"})

(require
 '[adzerk.bootlaces :refer :all]
 '[boot-semver.core :refer :all])

(task-options!
  pom {:project 'degree9/boot-bower
       :version (get-version)
       :description "boot-clj task for wrapping bower"
       :url         "https://github.com/degree9/boot-bower"
       :scm         {:url "https://github.com/degree9/boot-bower"}})

(deftask dev
  "Build boot-bower for development."
  []
  (comp
   (watch)
   (version :no-update true
            :minor 'inc
            :patch 'zero
            :pre-release 'snapshot)
   (target  :dir #{"target"})
   (build-jar)))

(deftask deploy
  "Build boot-bower and deploy to clojars."
  []
  (comp
   (version :minor 'inc
            :patch 'zero)
   (target  :dir #{"target"})
   (build-jar)
   (push-release)))

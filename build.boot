(set-env!
  :source-paths #{"src"}
  :dependencies '[[boot/core        "2.3.0"]
                  [adzerk/bootlaces "0.1.12"]
                  [org.clojars.hozumi/clj-commons-exec "1.2.0"]
                  [cheshire         "5.5.0"]])

(require '[adzerk.bootlaces :refer :all]
         '[degree9.boot-bower :refer :all])

(def +version+ "0.2.1")

(bootlaces! +version+)

(task-options!
  pom {:project 'degree9/boot-bower
       :version +version+
       :description "boot-clj task for wrapping bower"
       :url         "https://github.com/degree9/boot-bower"
       :scm         {:url "https://github.com/degree9/boot-bower"}})

(deftask dev
  "Build boot-bower local development."
  []
  (comp
   (watch)
   (build-jar)
   (speak)))

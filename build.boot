(set-env!
  :source-paths #{"src"}
  :dependencies '[[boot/core        "2.3.0"]
                  [adzerk/bootlaces "0.1.12"]
                  [me.raynes/conch  "0.8.0"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.0")

(bootlaces! +version+)

(task-options!
  pom {:project 'degree9/boot-bower
       :version +version+
       :description "boot-clj task for wrapping bower"
       :url         "https://github.com/degree9/boot-bower"
       :scm         {:url "https://github.com/degree9/boot-bower"}})

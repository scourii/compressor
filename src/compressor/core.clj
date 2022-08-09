(ns compressor.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]))

(defn- usage
  [options]
  (->> ["Usage compressor [options] file"
       ""
       "Options:"
       options
       ""]
       (str/join \newline)
       (println)))

(def cli-options 
  [["-c" "--compress" "Compress a file"]
   ["-h" "--help" "Print this help information"]])


(defn -main
  [& args]
  (let [{:keys [options arguments summary]} (parse-opts args cli-options)]
    (cond
      (:help options) 
      (usage summary)
      (:compress options)
      (println "ogey")
      (empty? options)
      (usage summary))))
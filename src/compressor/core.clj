(ns compressor.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [compressor.compress :as compress]))

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
    (condp apply [options]
      :help (usage summary)
      :compress (compress/compress-jpg arguments) ;placeholder
      (usage summary))))

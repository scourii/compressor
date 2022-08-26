(ns compressor.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [compressor.compress :as compress])
  (:gen-class))

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
  [["-c" "--compress PATH" "Compress a file"]
   ["-z" "--zip PATH" "Compress all the files in a path"]
   ["-h" "--help" "Print this help information"]
   ["-q" "--quality FLOAT" "Quality reduction when compressed"]
   ["-n" "--new PATH" "Path to new image"]
   ["-m" "--metadata" "Sets the metadata option to true"]])

(defn -main
  [& args]
  (let [{:keys [options summary]} (parse-opts args cli-options)]
    (condp apply [options]
      :help (usage summary)
      :compress (compress/compress-img (:compress options) :quality ^Float (:quality options) :new (:new options) :metadata (:metadata options))
      :zip (compress/compress-dir (:zip options) :output (:new options))
      (usage summary))))

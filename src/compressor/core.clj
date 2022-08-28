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
  (let [{:keys [options summary]} (parse-opts args cli-options)
        file (if (contains? options :zip) (:zip options) (:compress options))
        quality (:quality options)
        output (:new options)
        metadata? (:metadata options)]
    (condp apply [options]
      :help (usage summary)
      :compress (compress/compress-img file :quality ^Float quality :new output :metadata metadata?)
      :zip (compress/compress-dir file :output output)
      (usage summary))))

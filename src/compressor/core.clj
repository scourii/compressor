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
  [["-c" "--compress" "Compress a file"]
   ["-h" "--help" "Print this help information"]
   ["-q" "--quality" "Quality reduction when compressed"]
   ["-n" "--new" "Path to new image"]
   ["-m" "--metadata" "Sets the metadata option to true"]
   ["-i" "--image" "Path to image"]])

(defn -main
  [& args]
  (let [{:keys [options arguments summary]} (parse-opts args cli-options)
        [_ img quality path] arguments]
    (condp apply [options]
      :help (usage summary)
      :compress (compress/compress-img img :quality ^Float quality :new path :metadata (:metadata options))
      (usage summary))))

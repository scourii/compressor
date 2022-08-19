(ns compressor.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.string :as str]
            [compressor.compress :as compress]
            [criterium.core :as bench]))

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
  [["-C" "--convert" "Convert a file to a supported format."]
   ["-c" "--compress" "Compress a file"]
   ["-h" "--help" "Print this help information"]
   ["-q" "--quality" "Quality reduction when compressed"]
   ["-n" "--new" "Path to new image"]
   ["-i" "--image" "Path to image"]])

(defn -main
  [& args]
  (bench/with-progress-reporting (bench/quick-bench
  (let [{:keys [options arguments summary]} (parse-opts args cli-options)
        [_ image quality path] arguments]
    (condp apply [options]
      :help (usage summary)
      :compress (compress/compress-jpg image :quality ^Float quality :name path)
      :convert (println nil)
      (usage summary))) :verbose)))

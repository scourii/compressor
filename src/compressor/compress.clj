(ns compressor.compress
  (:import [java.awt.Image BufferedImage]))

(defn compress-jpg
  [jpg & {:keys [level]
          :or {level 0.7}}]
  (println jpg level))
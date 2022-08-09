(ns compressor.image
  (:import [javax.imageio IIOImage ImageIO]))

(defn file-extension
  [file]
  (second (re-find #"(\.[a-zA-Z0-9]+)$" file)))
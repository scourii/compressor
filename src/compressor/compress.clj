(ns compressor.compress
  (:require [clojure.java.io :as io])
  (:import [javax.imageio ImageIO IIOImage ImageWriteParam]))

(defn file-extension
  [file]
  (second (re-find #"([^.][a-zA-Z0-9]+)$" file)))

(defn compress-img
  [img & {:keys [quality new metadata]
          :or {quality 0.7 new img metadata false}}]
  (let [ext (file-extension img)
        reader (.next (ImageIO/getImageReadersByFormatName ext))
        writer (.next (ImageIO/getImageWritersByFormatName ext))]
    (with-open [input-stream (ImageIO/createImageInputStream (io/file img))
                output-stream (ImageIO/createImageOutputStream (io/file new))]
      (.setInput reader input-stream)
      (.setOutput writer output-stream)
      (let [image (if metadata (IIOImage. (.read reader 0) nil (.getImageMetadata reader 0)) (IIOImage. (.read reader 0) nil nil))
            params (doto (.getDefaultWriteParam writer)
                     (.setCompressionMode ImageWriteParam/MODE_EXPLICIT)
                     (.setCompressionQuality (read-string quality))
                     (.setOptimizeHuffmanTables true))]
        (.write writer nil image params))
      (.dispose reader)
      (.dispose writer))))

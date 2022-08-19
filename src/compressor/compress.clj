(ns compressor.compress
  (:require [clojure.java.io :as io])
  (:import [javax.imageio ImageIO IIOImage]
           [javax.imageio.plugins.jpeg JPEGImageWriteParam]))

(defn compress-jpg
  [jpg & {:keys [quality name]
          :or {quality 0.7 name jpg}}]
  (let [reader (.next (ImageIO/getImageReadersByFormatName "jpg"))
        writer (.next (ImageIO/getImageWritersByFormatName "jpg"))]
    (with-open [input-stream (ImageIO/createImageInputStream (io/file jpg))
                output-stream (ImageIO/createImageOutputStream (io/file name))]
      (.setInput reader input-stream)
      (.setOutput writer output-stream)
    (let [metadata (IIOImage. (.read reader 0) nil
                              (.getImageMetadata reader 0))
          params (doto (.getDefaultWriteParam writer)
                   (.setCompressionMode JPEGImageWriteParam/MODE_EXPLICIT)
                   (.setCompressionQuality (read-string quality))
                   (.setOptimizeHuffmanTables true))]
    (.write writer nil metadata params))
    (.dispose reader)
    (.dispose writer))))

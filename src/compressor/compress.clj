(ns compressor.compress
  (:require [clojure.java.io :as io])
  (:import [javax.imageio ImageIO IIOImage ImageWriteParam]
           ))

(defn file-extension
  [file]
  (second (re-find #"([^.][a-zA-Z0-9]+)$" file)))

(defn compress-jpg
  [jpg & {:keys [quality name]
          :or {quality 0.7 name jpg}}]
  (let [ext (file-extension jpg)
        reader (.next (ImageIO/getImageReadersByFormatName ext))
        writer (.next (ImageIO/getImageWritersByFormatName ext))]
    (with-open [input-stream (ImageIO/createImageInputStream (io/file jpg))
                output-stream (ImageIO/createImageOutputStream (io/file name))]
      (.setInput reader input-stream)
      (.setOutput writer output-stream)
    (let [metadata (IIOImage. (.read reader 0) nil
                              (.getImageMetadata reader 0))
          params (doto (.getDefaultWriteParam writer)
                   (.setCompressionMode ImageWriteParam/MODE_EXPLICIT)
                   (.setCompressionQuality (read-string quality))
                   (.setOptimizeHuffmanTables true))]
    (.write writer nil metadata params))
    (.dispose reader)
    (.dispose writer))))

(defn compress-jpg-rewrite
  [jpg & {:keys [quality name]
          :or {quality 0.7 name jpg}}]
  )

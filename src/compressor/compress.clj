(ns compressor.compress
  (:import [java.awt.Image BufferedImage]
           [clojure.java.io File]
           [javax.imageio ImageIO IIOImage]
           [javax.imageio.plugins.jpeg JPEGImageWriteParam]))

(defn compress-jpg
  [jpg & {:keys [quality name]
          :or {quality 0.7 name jpg}}]
  (let [reader (.next (ImageIO/getImageReadersByFormatName "jpg"))
        writer (.next (ImageIO/getImageWritersByFormatName "jpg"))
        metadata (IIOImage. (.read reader 0) nil
                            (.getImageMetadata reader 0))
        params (doto (.getDefaultWriteParm writer)
                 (.setCompressionMode JPEGImageWriteParam/MODE_EXPLICIT)
                 (.setCompressionQuality quality))]
    (with-open [input-stream (ImageIO/createImageInputStream (File. jpg))
                output-stream (ImageIO/createImageOutputStream (File. name))]
      (.setInput reader input-stream)
      (.setOutput writer output-stream))
    (.write writer nil metadata params)
    (finally
      (do (.dispose writer)
          (.dispose reader)))))

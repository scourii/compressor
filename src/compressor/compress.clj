(ns compressor.compress
  (:require [clojure.string :as str]
            [clojure.java.io :refer [file input-stream output-stream copy] :as io])
  (:import [javax.imageio ImageIO IIOImage ImageWriteParam]
           [java.util.zip ZipEntry ZipOutputStream]
           [java.io File]
           [org.apache.commons.compress.archivers.sevenz SevenZFile SevenZOutputFile]
           [org.apache.commons.io FilenameUtils]))

(defn file-extension
  [file]
  (second (re-find #"([^.][a-zA-Z0-9]+)$" file)))

(def compressors ["bzip2" "deflate" "gz" "lz4-framed" "lzma" "xz"])

(def archive-extensions
  {"bzip2"      ".tar.bz2"
   "deflate"    ".tar.gz"
   "gz"         ".tar.gz"
   "lz4-framed" ".tar.lz4"
   "lzma"       ".tar.lzma"
   "xz"         ".tar.xz"})

(defn create-archive-name
  [name output compressor]
  (let [extension   (get archive-extensions compressor)
        output-path (FilenameUtils/normalizeNoEndSeperator output)
        output      (if (= File/seperator output-path) "" output-path)
        output-file (str output File/seperator name extension)]
    output-file))

(defn compress-img
  [img & {:keys [quality new metadata]
          :or {quality 0.7 new img metadata false}}]
  (let [ext (file-extension img)
        reader (.next (ImageIO/getImageReadersByFormatName ext))
        writer (.next (ImageIO/getImageWritersByFormatName ext))]
    (with-open [input-stream (ImageIO/createImageInputStream (file img))
                output-stream (ImageIO/createImageOutputStream (file new))]
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

(defn zip-dir
  [dir & {:keys [output]
          :or {output dir}}]
  (with-open [zip (ZipOutputStream. (output-stream output))]
    (doseq [f (file-seq (file dir)) :when (.isFile f)]
      (.putNextEntry zip (ZipEntry. (str/replace-first (.getPath f) dir "")))
      (copy f zip)
      (.closeEntry zip))))




(ns compressor.compress
  (:require [clojure.string :as str]
            [clojure.java.io :refer [file input-stream output-stream copy] :as io]
            [compressor.compress :as compress])
  (:import [javax.imageio ImageIO IIOImage ImageWriteParam]
           [java.util.zip ZipEntry ZipOutputStream]
           [java.io File]
           [org.apache.commons.compress.compressors CompressorStreamFactory]
           [org.apache.commons.compress.archivers.tar TarArchiveOutputStream]
           [org.apache.commons.io FilenameUtils]
           [org.apache.commons.compress.utils IOUtils]))

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
  [output compressor]
  (let [extension   (get archive-extensions compressor)
        output-file (str output extension)]
    output-file))

(defn create-archive
  [input-files output compressor]
  (let [output-name       (create-archive-name output compressor)
        fo                (output-stream output-name)
        compressor-stream (.createCompressorOutputStream (CompressorStreamFactory.) compressor fo)
        tar-output-stream (TarArchiveOutputStream. compressor-stream)
        file-list         (mapv str (file-seq (file input-files)))]
    (doseq [input-name file-list]
      (let [folder? (.isDirectory (file input-name))]
        (println "Folder:" folder?)
        (doseq [f (if folder? (file-seq (file input-name)) [(file input-name)])]
          (when (and (.isFile f) (not= output-name (.getPath f)))
            (let [entry-name (.getPath (file input-name))
                  entry (.createArchiveEntry tar-output-stream f entry-name)]
              (.putArchiveEntry tar-output-stream entry)
              (when (.isFile f)
                (IOUtils/copy (input-stream f) tar-output-stream)
                (println "Copied " f)
                (.closeArchiveEntry tar-output-stream)))))))
    (.finish tar-output-stream)
    (.close tar-output-stream)
    output-name))

(defn compress-img
  [img & {:keys [quality new metadata]
          :or {quality 0.7 new img metadata false}}]
  (let [ext    (file-extension img)
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




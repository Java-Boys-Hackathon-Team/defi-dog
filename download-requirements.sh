#!/bin/bash

# ID документа (из URL)
DOC_ID="1-JOCyAptFwL30WpuQPYt4SX5FAIuRJt5nKTj6EHBL04"

# Имя временного и итогового файла
TEMP_FILE="temp_doc.txt"
OUTPUT_FILE="google_doc_clean.txt"

# Скачивание документа в текстовом формате
curl -sL "https://docs.google.com/document/d/${DOC_ID}/export?format=txt" -o "${TEMP_FILE}"

# Удаление пустых строк и сохранение в итоговый файл
grep -v '^[[:space:]]*$' "${TEMP_FILE}" > "${OUTPUT_FILE}"

# Удаление временного файла
rm -f "${TEMP_FILE}"

# Сообщение об успехе
echo "Документ сохранен без пустых строк в: ${OUTPUT_FILE}"

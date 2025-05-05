#!/bin/bash

# ID документа (из URL)
DOC_ID="1-JOCyAptFwL30WpuQPYt4SX5FAIuRJt5nKTj6EHBL04"

# Имя выходного файла
OUTPUT_FILE="project_requirements.txt"

# Скачивание документа в текстовом формате
curl -L "https://docs.google.com/document/d/${DOC_ID}/export?format=txt" -o "${OUTPUT_FILE}"

# Вывод сообщения об успехе
echo "Документ сохранен в файл: ${OUTPUT_FILE}"

#!/bin/bash

# Путь к каталогу (по умолчанию — текущий)
INPUT_DIR=${1:-.}

# Приведение к абсолютному пути
ROOT_DIR=$(realpath "$INPUT_DIR")

if [ ! -d "$ROOT_DIR" ]; then
  echo "❗ Указанный каталог не существует: $ROOT_DIR"
  exit 1
fi

# Исключаемые каталоги
EXCLUDED_DIRS_REGEX="(/build/|/out/|/node_modules/|/.git/|/.idea/|/target/|src/main/bundles)"

# Исключаемые расширения
EXCLUDED_EXTENSIONS=("class" "jar" "png" "jpg" "jpeg" "gif" "ico" "log" "js" "ts")

# Включаемые расширения
INCLUDED_EXTENSIONS=("java" "xml" "properties" "yml" "yaml" "html" "js" "ts" "gradle" "md")

# Название выходного файла
DIR_NAME=$(basename "$ROOT_DIR")
OUTPUT_FILE="$PWD/collected_code_${DIR_NAME}.txt"

# Очистка выходного файла
> "$OUTPUT_FILE"

# Поиск и обработка файлов
find "$ROOT_DIR" -type f | while read -r file; do
  # Проверка на включённые расширения
  ext="${file##*.}"
  include=0
  for allowed in "${INCLUDED_EXTENSIONS[@]}"; do
    if [[ "$ext" == "$allowed" ]]; then
      include=1
      break
    fi
  done
  if [ $include -eq 0 ]; then
    continue
  fi

  # Проверка на исключённые каталоги
  if [[ "$file" =~ $EXCLUDED_DIRS_REGEX ]]; then
    continue
  fi

  # Проверка на исключённые расширения
  for excluded in "${EXCLUDED_EXTENSIONS[@]}"; do
    if [[ "$ext" == "$excluded" ]]; then
      continue 2
    fi
  done

  # Относительный путь
  REL_PATH="${file#$ROOT_DIR/}"

  # Добавление метаданных и содержимого
  echo -e "\n\n==== FILE: $REL_PATH ====\n" >> "$OUTPUT_FILE"
  cat "$file" >> "$OUTPUT_FILE"
done

echo "✅ Сбор завершён. Результат в: $OUTPUT_FILE"

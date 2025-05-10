#!/bin/sh

# Проверка аргументов
if [ $# -lt 2 ]; then
  echo "Usage: $0 <repo_path> <ext1> [<ext2> ...]"
  exit 1
fi

REPO_PATH=$1
shift
EXTENSIONS="$@"

cd "$REPO_PATH" || { echo "Failed to enter repository at $REPO_PATH"; exit 1; }

# Построение регулярного выражения для grep по расширениям
PATTERN=""
for ext in $EXTENSIONS; do
  [ -n "$PATTERN" ] && PATTERN="$PATTERN\\|"
  PATTERN="$PATTERN\\.$ext\$"
done

# Поиск последнего коммита, содержащего изменения в нужных файлах
COMMIT=$(git log --name-only --pretty=format:"%H" | awk -v pattern="$PATTERN" '
  /^[0-9a-f]{40}$/ { commit=$0; next }
  $0 ~ pattern { print commit; exit }')

if [ -z "$COMMIT" ]; then
  echo "No matching commits found."
  exit 1
fi

# Выводим diff и полный хэш коммита
echo "Commit: $COMMIT"
git show "$COMMIT"

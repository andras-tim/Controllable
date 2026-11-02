#!/bin/sh
# Fills a local Maven repository with the MrCrayfish artifacts, which are only on GitHub Packages, behind a token.
# Framework and the SDL binding come from the public releases, checked by sha256.
# Usage: LOCAL_MAVEN=<dir> sh tools/local-maven.sh
set -eu

CONTROLLABLE_URL='https://mediafilez.forgecdn.net/files/7943/200/controllable-forge-1.20.1-0.21.9.jar'
CONTROLLABLE_SHA256='937f628547fb077ac207ecf1180541c707c26beafa3e98b1ab239ef22bdf2b80'
FRAMEWORK_URL='https://mediafilez.forgecdn.net/files/7573/740/framework-forge-1.20.1-0.8.0.jar'
FRAMEWORK_SHA256='37c267f46d8b8dfa81bae8c9fa9fa6467e456cadecae10722ee91b43437f25a0'
SDL_VERSION='2.30.12-1.1.0'
FRAMEWORK_VERSION='1.20.1-0.8.0'

work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT

fetch() {
    curl -fsSL "$1" -o "$work/$3"
    echo "$2  $work/$3" | sha256sum -c --quiet -
}

# publish <artifact> <version> <jar>
publish() {
    dir="$LOCAL_MAVEN/com/mrcrayfish/$1/$2"
    mkdir -p "$dir"
    cp "$3" "$dir/$1-$2.jar"
    cat > "$dir/$1-$2.pom" <<EOF
<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.mrcrayfish</groupId>
  <artifactId>$1</artifactId>
  <version>$2</version>
</project>
EOF
}

fetch "$FRAMEWORK_URL" "$FRAMEWORK_SHA256" framework.jar
fetch "$CONTROLLABLE_URL" "$CONTROLLABLE_SHA256" controllable.jar
unzip -p "$work/controllable.jar" "META-INF/jarjar/controllable-sdl-$SDL_VERSION.jar" > "$work/sdl.jar"

publish framework-forge "$FRAMEWORK_VERSION" "$work/framework.jar"
# The common project only needs the API classes, which the Forge jar has with the same names
publish framework-common "$FRAMEWORK_VERSION" "$work/framework.jar"
publish controllable-sdl "$SDL_VERSION" "$work/sdl.jar"

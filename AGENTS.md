# Controllable fork for mc.dras.hu - agent instructions

## Stay clean against upstream

This repo is a fork of [MrCrayfish/Controllable](https://github.com/MrCrayfish/Controllable). The work branch is `dras`,
on top of `multiloader/1.20.X`. Validate every change against the current upstream, not against the last known base:

```bash
git fetch upstream multiloader/1.20.X
git rev-list --count HEAD..upstream/multiloader/1.20.X                   # how far behind
git merge-tree --write-tree --name-only HEAD upstream/multiloader/1.20.X # trial merge, lists the conflicts
git diff --stat "$(git merge-base upstream/multiloader/1.20.X HEAD)" HEAD
```

- Keep changes in upstream files small and new code in new files, so the next merge stays easy.
- Don't change upstream code without a need. No site or pack specific value in the mod.
- Report the result with the change: commits behind, conflicting files, and what the change added to them.

## Repo overview

A Forge 1.20.1 client mod for game controllers, written in `common/`, loaded through `forge/`. The Fabric project stays in
the tree for easier merges, but it is not part of the build.

## Running

The toolchain runs in a container, nothing is installed in the home directory:

```bash
LOCAL_MAVEN=~/.cache/mc-dras/controllable-maven sh tools/local-maven.sh
docker run --rm -u "$(id -u):$(id -g)" -e HOME=/tmp -e GRADLE_USER_HOME=/cache -e LOCAL_MAVEN=/maven \
  -v ~/.cache/mc-dras/controllable-maven:/maven:ro -v ~/.cache/mc-dras/gradle-controllable:/cache \
  -v "$PWD:/work" -w /work eclipse-temurin:17-jdk sh ./gradlew --no-daemon :forge:build -x test
```

The result is `forge/build/libs/dras-gamepad-forge-<version>.jar`. The name must not contain `controllable`: Amethyst
shows a crash warning and starts a delete thread for any such jar in `mods/`.

## Commit conventions

Upstream style, `feat:` or `fix:` with an optional scope, and `fork:` for what only makes sense in this fork, so the
features can go upstream later. Brief bullet-list bodies. No `Co-Authored-By` or similar trailers. Commits and tags are
signed. Release tags are `<upstream version>-dras.<n>`, each with a GitHub release and a bullet changelog.

## Docs

A README states what is, this file states what to do. Each fork commit carries its reason in its message, the list
below is the overview: keep it in step with the commits.

## What the fork changes and why

- Android launchers (PojavLauncher and its forks, such as Amethyst) run the game with `os.name=Linux`, so the SDL2
  backend loads a glibc native that cannot work there and crashes at random. On these launchers the fork uses GLFW.
- The gamepad works as on a console: the D-pad moves the focus between widgets, `A` confirms, `B` goes back on every
  screen (the inventory included), `Y` is quick move, and on screens the triggers are the mouse buttons.
- Fixes found on the way: D-pad navigation to small buttons under a wide one, the focus on checkboxes, the empty
  radial menu on the first start.
- Own jar name, version and issue tracker, and a build without the GitHub Packages token.

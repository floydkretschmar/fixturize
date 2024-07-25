#!/usr/bin/env bash

function _bump_version() {
  PREVIOUS_TAG=${1}
  BUMP_TYPE=${2}

  VERSION=${PREVIOUS_TAG#v}
  IFS='.' read -r -a VERSION_PARTS <<< "$VERSION"
  MAJOR=${VERSION_PARTS[0]}
  MINOR=${VERSION_PARTS[1]}
  PATCH=${VERSION_PARTS[2]}

  case "${BUMP_TYPE}" in
    "bump-major")
      MAJOR=$((MAJOR + 1))
      MINOR=0
      PATCH=0
      ;;
    "bump-minor")
      MINOR=$((MINOR + 1))
      PATCH=0
      ;;
    "bump-patch")
      PATCH=$((PATCH + 1))
      ;;
  esac

  NEW_VERSION="$MAJOR.$MINOR.$PATCH"
  echo "New version: $NEW_VERSION"
  echo "NEW_VERSION=$NEW_VERSION" >> "$GITHUB_OUTPUT"
}

function _create_new_tag() {
  NEW_TAG="v${1}"
  git config --global user.name 'github-actions'
  git config --global user.email 'github-actions@github.com'
  git tag "$NEW_TAG"
  git push origin "$NEW_TAG"
  echo "Newly created tag $NEW_TAG"
}

function _create_release_body() {
  PREVIOUS_TAG=${1}
  NEW_VERSION=${2}
  MESSAGE_BODY=${3}
  GITHUB_REPOSITORY=${4}
  if [ "$PREVIOUS_TAG" != "v0.0.0" ]; then
    RELEASE_BODY="$MESSAGE_BODY\n\nCommits since previous release: [link](https://github.com/${GITHUB_REPOSITORY}/compare/${PREVIOUS_TAG}...v${NEW_VERSION})"
  else
    RELEASE_BODY="$MESSAGE_BODY\n\nAll commits associated with this release: [link](https://github.com/${GITHUB_REPOSITORY}/commits/v${NEW_VERSION})"
  fi

  echo "RELEASE_BODY=$RELEASE_BODY" >> "$GITHUB_OUTPUT"
  echo "$RELEASE_BODY"
}

function _extract_bump() {
  COMMIT_MESSAGE=$(git log -1 --pretty=%B)

  if [[ $COMMIT_MESSAGE =~ ^\[bump-(major|minor|patch)\] ]]; then
    BUMP_TYPE=$(echo $COMMIT_MESSAGE | grep -oP '\[bump-(major|minor|patch)\]' | tr -d '[]')
    MESSAGE_BODY=$(echo $COMMIT_MESSAGE | sed -e "s/\[bump-(major|minor|patch)\] //")

    echo "BUMP_TYPE=$BUMP_TYPE" >> "$GITHUB_OUTPUT"
    echo "MESSAGE_BODY=$MESSAGE_BODY" >> "$GITHUB_OUTPUT"
  else
    echo "MESSAGE_BODY=$COMMIT_MESSAGE" >> "$GITHUB_OUTPUT"
  fi

  echo "Extracted bump: $BUMP_TYPE"
  echo "Extracted message: $COMMIT_MESSAGE"
}

function _get_previous_tag() {
  PREVIOUS_TAG=$(git describe --tags --abbrev=0 HEAD^ || echo "v0.0.0")
  echo "PREVIOUS_TAG=$PREVIOUS_TAG" >> "$GITHUB_OUTPUT"
  echo "Previous tag: $PREVIOUS_TAG"
}

CMD=${1:-}
shift || true
case ${CMD} in
  bump-version) _bump_version "$1" "$2" ;;
  create-new-tag) _create_new_tag "$1" ;;
  create-release-body) _create_release_body "$1" "$2" "$3" "$4" ;;
  extract-bump) _extract_bump ;;
  get-previous-tag) _get_previous_tag ;;
esac


# General
We use [semantic versioning](https://semver.org).

We publish our snapshots on [GitHub Packages](https://github.com/orgs/9tigerio/packages?repo_name=db2rest).

We publish our releases on [Maven Central Portal](https://central.sonatype.com/) (Sonatype) under our namespace [io.9tiger](https://central.sonatype.com/namespace/io.9tiger)

We publish a **snapshot** `-SNAPSHOT` version at any time by using the corresponding GitHub Action.

We publish a **release** version to [Maven Central Portal](https://central.sonatype.com/namespace/io.9tiger) at any time by using the corresponding GitHub Action.

After a release, we can also build and publish a Docker container image to our DockerHub (working account for now: [kdhrubo](https://hub.docker.com/r/kdhrubo/db2rest)) via the corresponding GitHub Action.

# GPG Key signing steps
Follow the guide at [Maven Central Portal distributing your public key](https://central.sonatype.org/publish/requirements/gpg/#distributing-your-public-key)

# Snapshots

Using GitHub workflow `.github/workflows/snapshot.yml`

# Make a New Release

Using GitHub workflow `.github/workflows/maven-central-publish.yml`

# Make a Docker image

Using GitHub workflow `.github/workflows/dockerhub-publish.yml` which will download the Maven Central packaged released version and build a Docker image and publish to DockerHub

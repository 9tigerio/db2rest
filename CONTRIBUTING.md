Thanks for showing interest to contribute to DB2Rest 💖, you rock!

When it comes to open source, there are different ways you can contribute, all
of which are valuable. Here's a few guidelines that should help you as you
prepare your contribution.

## Setup the Project

The following steps will get you up and running to contribute to DB2Rest:

1. Fork the repo (click the <kbd>Fork</kbd> button at the top right of
   [this page on our GitHub](https://github.com/9tigerio/db2rest))

2. Clone your fork locally.
    
   Use your IDE's Git features, or a terminal like this:
    ```sh
    git clone https://github.com/<your_github_username>/db2rest.git
    cd db2rest
    ```

1. Setup all the dependencies and packages by running:
   ```sh
   mvnw compile
   ```
   This command will install dependencies using the [maven wrapper](https://maven.apache.org/wrapper/) script.

> If you run into any issues during this step, kindly reach out to the DB2Rest 
> team here: [![](https://dcbadge.vercel.app/api/server/kqeDatPGwU?theme=discord)](https://discord.gg/kqeDatPGwU)

## Development
DB2Rest uses a [monorepo](https://en.wikipedia.org/wiki/Monorepo) structure. To improve our development process, we've set up tooling and systems.
DB2Rest uses a [Maven modular project structure](https://maven.apache.org/guides/mini/guide-multiple-modules-4.html) with a parent `pom.xml` file, and child pom's in subfolders.

### Tooling

- [Maven](https://maven.apache.org/) to manage packages and dependencies.
- [Docker](https://docs.docker.com/) We use Docker for spinning up test containers used in testing.
  On Linux, ensure docker is installed.
  On Windows, [Docker Desktop](https://www.docker.com/products/docker-desktop/) is needed.  
- [Spring Test](https://docs.spring.io/spring-framework/reference/testing/integration.html) for integration testing of our Java components.

### Testing

Database test configurations are found under: `\db2rest\api-rest\src\test\java\com\homihq\db2rest`.

### Commands

**`mvnw build`**: builds and compiles all DB2Rest packages into `/target` folders.

**`mvnw test`**: run tests for all DB2Rest packages.

**`mvnw verify`**: builds and then starts integration testing using test containers on docker.

### Release Process

See [.github/RELEASING.md](https://github.com/9tigerio/db2rest/.github/RELEASING.md)

## Think you found a bug?

Create a [New GitHub Issue](https://github.com/9tigerio/db2rest/issues/new/choose) and please conform to the issue template and provide a clear path to reproduction
with a code example. 

## Proposing new or changed API?

Please provide thoughtful comments and some sample API code. Proposals that
don't line up with our roadmap or don't have a thoughtful explanation will be
closed.

## Making a Pull Request?

Pull requests need only the :+1: of two or more collaborators to be merged; when
the PR author is a collaborator, that counts as one.

### Commit Convention

Before you create a Pull Request, please check whether your commits comply with
the commit conventions used in this repository.

When you create a commit we kindly ask you to follow the convention
`category(scope or module): message` in your commit message while using one of
the following categories:

- `feature`: all changes that introduce completely new code or new
  features
- `fix`: changes that fix a bug (ideally you will additionally reference an
  issue if present)
- `refactor`: any code related change that is not a fix nor a feature
- `docs`: changing existing or creating new documentation (i.e. README, Javadocs, code
  comments, etc.
- `build`: all changes regarding the build of the software, changes to
  dependencies or the addition of new dependencies
- `test`: all changes regarding tests (adding new tests or changing existing
  ones)
- `ci`: all changes regarding the configuration of continuous integration (i.e.
  github actions, ci system)
- `chore`: all changes to the repository that do not fit into any of the above
  categories

If you are interested in the detailed specification you can visit
https://www.conventionalcommits.org/ or check out the
[Angular Commit Message Guidelines](https://github.com/angular/angular/blob/22b96b9/CONTRIBUTING.md#-commit-message-guidelines).

### Steps to PR

1. Fork of the DB2Rest repository and clone your fork

2. Create a new branch out of the `master` branch. We follow the branch name convention
   `[type/scope]`. For example `fix/rest-common` or `docs/api-rest`. `type`
   can be either `docs`, `fix`, `feat`, `build`, or any other conventional
   commit type. `scope` is just a short id that describes the scope of work.

3. Make and commit your changes following the
   [commit convention](https://github.com/9tigerio/db2rest/blob/main/CONTRIBUTING.md#commit-convention).
   As you develop, you can run `mvnw <module> compile` and
   `mvnw <module> test` to make sure everything works as expected. Please
   note that you might have to run `mvnw compile` first in order to build all
   dependencies for testing.

### Tests

All commits that fix bugs or add features SHOULD add a test.

## Want to write a blog post or tutorial

That would be amazing! Reach out to the core team here:
https://discord.gg/kqeDatPGwU. We would love to support you any way we can.

## Want to help improve the docs?

Our docsite lives in a
[separate repo](https://github.com/9tigerio/db2rest-web). If you're
interested in contributing to the documentation, check out the
[docsite contribution guide](https://github.com/9tigerio/db2rest-web/blob/main/CONTRIBUTING.md).

## License

By contributing your code to the DB2Rest GitHub repository, you agree to
license your contribution under the [Apache-2.0 license](https://github.com/9tigerio/db2rest/blob/main/LICENSE).
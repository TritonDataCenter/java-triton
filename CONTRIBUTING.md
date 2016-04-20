# Contributing

We love pull requests from everyone.

Fork, then clone the repo:

    git clone git@github.com:your-username/joyent/java-triton.git

For running integration tests, you will need an account on a Triton system this could
be the [Joyent Public Cloud](https://www.joyent.com/public-cloud), an 
[on-premise Triton/SmartDataCenter installation](https://github.com/joyent/sdc), or
an installation of [COAL for local testing](https://github.com/joyent/sdc/blob/master/docs/developer-guide/coal-setup.md). 

Set up your machine:

You will need to set the following environment variables (or their aliases):
    TRITON_URL, TRITON_USER, TRITON_KEY_ID, TRITON_KEY_PATH

It may be useful to install the [node.js triton utility](https://www.npmjs.com/package/triton) 
to verify that you can connect to Triton before running the tests:

    npm install -g triton
    
Once it is installed correctly you can run the `triton env` command as shown 
below to setup your environment or to display your already configured environment.
    
    triton env

Make sure the unit tests pass:

    mvn -DskipIT=true clean verify
    
Make sure all tests including the integration tests pass:

    mvn verify

Make your change. Add tests both unit and integration tests for your change. 
Make sure that new tests match the format of existing tests. Make sure that 
all tests and style checks pass:

    mvn checkstyle:checkstyle -Dcheckstyle.skip=false verify

Add your changes to the CHANGELOG.md and commit.

Push to your fork and [submit a pull request][pr].

[pr]: https://github.com/joyent/java-triton/compare/

At this point you're waiting on us. We like to at least comment on pull requests
within three business days (and, typically, one business day). We may suggest
some changes or improvements or alternatives.

Some things that will increase the chance that your pull request is accepted:

* Filing a github issue describing the improvement or the bug before you start work.
* Write tests.
* Follow the style defined in (checkstyle.xml)[checkstyle.xml].
* Write a good commit message.

version in ThisBuild := sys.env.getOrElse("version", default = "2.6.0-SNAPSHOT").stripPrefix("v")

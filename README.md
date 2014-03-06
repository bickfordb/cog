# cog

Cog is a leiningen plugin to watch targets and execute shell build commands whenever they change

## Usage

Put `[cog "1.0.0"]` into the `:plugins` vector of your project.clj.

```clojure
(defproject my-website "1.0.0"
  ...
  :cog {:targets {"javascript" {:watch "resources/public/js"
                                :exec ["bash" "scripts/build-javascript.sh"]}}})
```

Cog will automatically start with the "ring server" leiningen command

### Commands

* Build all targets: `lein cog build`
* Force a target to run: `lein cog build js`
* Watch all targets: `lein cog watch`

## License

Copyright 2014 Brandon Bickford.  All rights reserved

This work is licensed under the GPL version 3 or later.  Refer to http://www.gnu.org for a copy

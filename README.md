# Java API for StrictYAML Processing

An implementation inspired by [StrictYAML](https://github.com/crdoconnor/strictyaml) and influenced by [Java API for JSON Processing](https://github.com/eclipse-ee4j/jsonp).

## Unsupported Features
- Implicit typing
- Duplicate keys
- Explicit tags
- Node anchors and refs
- Flow style

See [What YAML features does StrictYAML remove?](https://hitchdev.com/strictyaml/features-removed/) for more details.

Presence of a removed feature raises an exception during parsing.

## Ignored Features
- Directives
- Comments

Ignored features are silently skipped by a parser. No exception is thrown.

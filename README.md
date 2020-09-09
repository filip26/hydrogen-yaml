# Java API for YAML Processing

An implementation inspired by [StrictYAML](https://github.com/crdoconnor/strictyaml). 

Plain YAML is a super simple human-readable data-serialization language. Most of YAML 1.x features are not intentionally supported.

## Unsupported Features
- Implicit typing
- Duplicate keys
- Explicit tags
- Node anchors and refs
- Flow style
- Complex mapping keys
- Multi-documents
- Directives

See [What YAML features does StrictYAML remove?](https://hitchdev.com/strictyaml/features-removed/) for more details.

Presence of a removed feature raises an exception during parsing.

## Roadmap
- [ ] 0.1 Document API
- [ ] 0.2 Writer API
- [ ] 0.3 Parser API
- [ ] 0.4 TBD

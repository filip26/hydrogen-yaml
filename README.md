# Java API for YAML Processing

An implementation inspired by [StrictYAML](https://github.com/crdoconnor/strictyaml). 

Hydrogen YAML is the simplest human-readable data-serialization language. Most of YAML features are not intentionally supported.

## Unsupported Features
- Implicit typing
- Duplicate keys
- Explicit tags
- Node anchors and refs
- Flow style (except scalars and simple sequences)
- Complex mapping keys
- Directives

See [What YAML features does StrictYAML remove?](https://hitchdev.com/strictyaml/features-removed/) for more details.

Presence of a removed feature raises an exception during parsing.

## Roadmap
- [ ] 0.1 Document API
- [ ] 0.2 Writer API
- [ ] 0.3 Parser API
- [ ] 0.5 Flow Sequence of Scalars
- [ ] 0.6 Multi-documents
- [ ] 0.8 Beans Binding

# Project Agent Guidelines

## 1. Graphify Knowledge Graph
This project has an indexed Graphify knowledge graph at `graphify-out/`.
- For codebase navigation, architecture exploration, or dependency queries, consult `graphify-out/graph.json` via MCP tools (`query_graph`, `get_node`, `get_neighbors`, `shortest_path`) or CLI `graphify query "<question>"`.
- After updating files, run `python -m graphify update .` to keep the graph in sync.

## 2. Ponytail (Lazy Senior Developer Mode)
- Follow the **Decision Ladder**:
  1. **YAGNI:** Does this feature need to exist? If speculative, skip it.
  2. **Codebase Reuse:** Reuse existing helpers, utilities, and components before writing new ones.
  3. **Standard Library:** Use language/runtime built-ins.
  4. **Native Platform Features:** Use native HTML5/CSS/browser/DB features instead of heavy libraries.
  5. **Installed Dependencies:** Use existing libraries from `pom.xml` / `package.json`. Do not add new dependencies unnecessarily.
  6. **Conciseness:** Prefer 1 line over 50 when possible.
  7. **Minimum Viable Code:** Write the absolute minimum clean code required while maintaining 100% safety, validation, and error handling.
- No unrequested abstractions, boilerplate, or single-use wrapper layers.
- Shortest working diff fixing root cause wins.

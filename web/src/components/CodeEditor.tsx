"use client";

import Editor from "react-simple-code-editor";
import Prism from "prismjs";
import "prismjs/components/prism-bash";
import "prismjs/components/prism-python";
import "prismjs/components/prism-javascript";
import "prismjs/components/prism-json";

const languageNames: Record<string, string> = {
  Bash: "bash",
  Shell: "bash",
  Python: "python",
  JavaScript: "javascript",
  Other: "plain",
};

export function CodeEditor({ id, value, language, onChange, required = false }: { id: string; value: string; language: string; onChange: (value: string) => void; required?: boolean }) {
  const syntax = languageNames[language] ?? language;
  return <div className="code-editor">
    <Editor
      value={value}
      onValueChange={onChange}
      highlight={(code) => Prism.highlight(code, Prism.languages[syntax] ?? {}, syntax)}
      padding={12}
      tabSize={2}
      insertSpaces
      textareaId={id}
      required={required}
      textareaClassName="code-editor-input"
      preClassName="code-editor-highlight"
      style={{ minHeight: 94, fontFamily: "ui-monospace, SFMono-Regular, Consolas, monospace", fontSize: 12, lineHeight: 1.65, background: "#080d0a", color: "#d6e8dc", borderRadius: 7 }}
    />
  </div>;
}

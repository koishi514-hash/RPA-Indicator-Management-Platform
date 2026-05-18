import React, { useState, useEffect } from 'react';
import Editor from '@monaco-editor/react';

// 1. 扩展语言类型定义
type LanguageType =
    | 'javascript'
    | 'java'
    | 'python'
    | 'sql'
    | 'json';

// 定义 Props 类型
interface CodeEditorProps {
    language?: string;
    value?: string;
    onChange?: (value: string) => void;
    height?: string;
    // 新增：是否显示语言选择器
    showLanguageSelector?: boolean;
}

// 2. 语言选项配置
const LANGUAGE_OPTIONS = [
    { value: 'java', label: 'Java' },
    { value: 'javascript', label: 'JavaScript' },
    { value: 'python', label: 'Python' },
    { value: 'sql', label: 'SQL' },
    { value: 'json', label: 'JSON' },
];

const CodeEditor: React.FC<CodeEditorProps> = ({
    language: propLanguage = 'java',
    value: propValue = '',
    onChange,
    height = '400px',
    showLanguageSelector = true, // 默认显示语言选择器
}) => {
    // 状态管理
    const [language, setLanguage] = useState<LanguageType>(
        (LANGUAGE_OPTIONS.some(opt => opt.value === propLanguage)
            ? propLanguage
            : 'java') as LanguageType
    );
    const [code, setCode] = useState<string>(propValue);

    // 监听外部传入的 value 变化
    useEffect(() => {
        setCode(propValue);
    }, [propValue]);

    // 监听外部传入的 language 变化
    useEffect(() => {
        if (LANGUAGE_OPTIONS.some(opt => opt.value === propLanguage)) {
            setLanguage(propLanguage as LanguageType);
        }
    }, [propLanguage]);

    // 切换语言的回调
    const handleLanguageChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const newLanguage = e.target.value as LanguageType;
        setLanguage(newLanguage);
    };

    // 编辑器内容变化回调
    const handleEditorChange = (value: string | undefined) => {
        const newValue = value || '';
        setCode(newValue);
        onChange?.(newValue);
    };

    return (
        <div style={{
            padding: 20,
            border: '1px solid #d9d9d9',
            borderRadius: 8,
            backgroundColor: '#fff',
            boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
        }}>
            {/* 语言切换区域 */}
            {showLanguageSelector && (
                <div style={{
                    marginBottom: 15,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between'
                }}>
                    <div>
                        <label style={{
                            marginRight: 10,
                            fontWeight: 'bold',
                            color: '#333'
                        }}>
                            选择编程语言：
                        </label>
                        <select
                            value={language}
                            onChange={handleLanguageChange}
                            style={{
                                padding: '8px 12px',
                                fontSize: 14,
                                border: '1px solid #d9d9d9',
                                borderRadius: 4,
                                outline: 'none',
                                cursor: 'pointer',
                                backgroundColor: '#fff',
                            }}
                        >
                            {LANGUAGE_OPTIONS.map(option => (
                                <option key={option.value} value={option.value}>
                                    {option.label}
                                </option>
                            ))}
                        </select>
                    </div>
                    <div style={{
                        fontSize: 12,
                        color: '#666',
                        fontStyle: 'italic'
                    }}>
                        支持语法高亮和代码编辑
                    </div>
                </div>
            )}

            {/* Monaco 编辑器核心 */}
            <Editor
                height={height}
                language={language}
                value={code}
                onChange={handleEditorChange}
                options={{
                    fontSize: 14,
                    minimap: { enabled: false },
                    scrollBeyondLastLine: false,
                    wordWrap: 'on',
                    lineNumbers: 'on',
                    automaticLayout: true,
                    theme: 'vs-light',
                    folding: true,
                    showFoldingControls: 'mouseover',
                    tabSize: 2,
                    insertSpaces: true,
                    detectIndentation: true,
                    renderWhitespace: 'boundary',
                    renderControlCharacters: true,
                    scrollbar: {
                        vertical: 'visible',
                        horizontal: 'visible',
                        useShadows: false
                    }
                }}
                onMount={(editor) => {
                    console.log('Monaco Editor 已挂载, 当前语言:', language);
                }}
                loading={
                    <div style={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        height: '400px',
                        color: '#666'
                    }}>
                        编辑器加载中...
                    </div>
                }
            />
        </div>
    );
};

export default CodeEditor;
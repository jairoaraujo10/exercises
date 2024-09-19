import React, {useEffect, useRef, useState} from 'react';
import {PlusOutlined} from '@ant-design/icons';
import {InputRef, Tooltip} from 'antd';
import {Flex, Input, Tag, theme} from 'antd';
import {TagModel} from "../../interfaces/Exercise.tsx";
import type {GlobalToken} from "antd/es/theme/interface";
import TagComp from "../TagElement";

const tagInputStyle: React.CSSProperties = {
    width: 64,
    height: 22,
    marginInlineEnd: 8,
    verticalAlign: 'top',
};

const tagPlusStyle = (token: GlobalToken): React.CSSProperties => {
    return {
        height: 22,
        background: token.colorBgContainer,
        borderStyle: 'dashed'
    }
};

interface TagsListProps {
    tags: TagModel[]
    onUpdate: (tags: TagModel[]) => void;
}

const TagsEditableList: React.FC<TagsListProps> = (input) => {
    const {token} = theme.useToken();
    const [inputVisible, setInputVisible] = useState(false);
    const [inputValue, setInputValue] = useState('');
    const [editInputIndex, setEditInputIndex] = useState(-1);
    const [editInputValue, setEditInputValue] = useState('');
    const inputRef = useRef<InputRef>(null);
    const editInputRef = useRef<InputRef>(null);
    const [tags, setTags] = useState<string[]>(input.tags.map(tag => tag.value));

    useEffect(() => {
        if (inputVisible) {
            inputRef.current?.focus();
        }
    }, [inputVisible]);

    useEffect(() => {
        editInputRef.current?.focus();
    }, [editInputValue]);

    function updateTags(newTags: string[]) {
        setTags(newTags)
        input.onUpdate(newTags.map(tag => {
            return {
                value: tag
            }
        }))
    }

    function addTags(newTags: string[]) {
        updateTags(newTags)
    }

    const handleClose = (removedTag: string) => {
        const newTags = tags.filter((tag) => tag !== removedTag);
        updateTags(newTags)
    };

    const showInput = () => {
        setInputVisible(true);
    };

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setInputValue(e.target.value);
    };

    const handleInputConfirm = async () => {
        if (inputValue && !tags.includes(inputValue)) {
            addTags([...tags, inputValue]);
        }
        setInputVisible(false);
        setInputValue('');
    };

    const handleEditInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setEditInputValue(e.target.value);
    };

    const handleEditInputConfirm = () => {
        const newTags = [...tags];
        newTags[editInputIndex] = editInputValue;
        addTags(newTags);
        setEditInputIndex(-1);
        setEditInputValue('');
    };

    return (
        <Flex gap="4px 0" wrap>
            {tags.map<React.ReactNode>((tag, index) => {
                if (editInputIndex === index) {
                    return (
                        <Input
                            ref={editInputRef}
                            key={tag}
                            size="small"
                            style={tagInputStyle}
                            value={editInputValue}
                            onChange={handleEditInputChange}
                            onBlur={handleEditInputConfirm}
                            onPressEnter={handleEditInputConfirm}
                        />
                    );
                }
                return (
                    <>
                        <Tooltip title={tag} key={tag}>
                            <TagComp tag={{value: tag}} closable={true} onClose={() => handleClose(tag)}/>
                        </Tooltip>
                    </>
                );
            })}
            {inputVisible ? (
                <Input
                    ref={inputRef}
                    type="text"
                    size="small"
                    style={tagInputStyle}
                    value={inputValue}
                    onChange={handleInputChange}
                    onBlur={handleInputConfirm}
                    onPressEnter={handleInputConfirm}
                />
            ) : (
                <Tag style={tagPlusStyle(token)} icon={<PlusOutlined/>} onClick={showInput}>
                    Add Tag
                </Tag>
            )}
        </Flex>
    );
};

export default TagsEditableList;
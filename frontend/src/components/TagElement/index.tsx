import React, {useState} from "react";
import {TagModel} from "../../interfaces/Exercise.tsx";
import {Tag} from "antd";

interface TagProps {
    tag: TagModel
    closable: boolean
    onClose?: () => void
}

const tagStyle = (): React.CSSProperties => {
    return {
        userSelect: 'none'
    }
};

const TagComp: React.FC<TagProps> = (inputTag) => {
    const [tag] = useState<string>(inputTag.tag.value);
    const isLongTag = tag.length > 20;
    return (
        <Tag key={tag} color="blue" style={tagStyle()} closable={inputTag.closable} onClose={inputTag.onClose}>
            {isLongTag ? `${tag.slice(0, 20)}...` : tag}
        </Tag>
    )
};

export default TagComp;
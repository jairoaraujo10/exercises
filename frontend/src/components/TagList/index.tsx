import React, {useState} from 'react';
import {Tooltip} from 'antd';
import {TagModel} from "../../interfaces/Exercise.tsx";
import TagComp from "../TagElement";

interface TagsListProps {
    tags: TagModel[]
}

const TagList: React.FC<TagsListProps> = (inputTags) => {
    const [tags] = useState<TagModel[]>(inputTags.tags);

    return (
        <>
            {tags.map<React.ReactNode>((tag) => {
                return (
                    <>
                        <Tooltip title={tag.value} key={tag.value}>
                            <TagComp tag={tag} closable={false}/>
                        </Tooltip>
                    </>
                )
            })}
        </>
    );
};

export default TagList;
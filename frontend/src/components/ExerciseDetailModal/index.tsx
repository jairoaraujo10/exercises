import React, {useState} from "react";
import {Button, Card, Input, List, message, Modal, Radio} from "antd";
import {EditOutlined, DeleteOutlined, SaveOutlined, CloseOutlined} from '@ant-design/icons';
import {FullExercise, TagModel} from "../../interfaces/Exercise.tsx";
import TagsEditableList from "../TagsEditableList";
import TagList from "../TagList";
import {DeleteExercise, UpdateExercise} from "../../context/AuthContext/utils.tsx";

interface ExerciseDetailModalProps {
    open: boolean;
    onDelete: () => void;
    onClose: () => void;
    exercise: FullExercise;
}

const getAnswerLetter = (index: number): string => {
    return String.fromCharCode(97 + index) + '.';
}

const deepCopy = (obj: FullExercise) => {
    return JSON.parse(JSON.stringify(obj));
}

const ExerciseDetailModal: React.FC<ExerciseDetailModalProps> = ({open, onDelete, onClose, exercise}) => {
    const [isEditing, setIsEditing] = useState(false);
    const [savedExercise, setSavedExercise] = useState<FullExercise>(deepCopy(exercise));
    const [editingExercise, setEditingExercise] = useState<FullExercise>(deepCopy(savedExercise));

    const handleEditClick = () => {
        setIsEditing(true);
    };

    async function handleSaveClick() {
        const response = await UpdateExercise(editingExercise)
        if(response.data) {
            setIsEditing(false)
            setSavedExercise(editingExercise)
            message.success(response.data.message)
        } else {
            if(response.error) {
                message.error(response.error.message)
            }
            message.error("Unknown error")
        }
    }

    async function handleDeleteClick() {
        const response = await DeleteExercise(editingExercise)
        if (response.data) {
            message.success(response.data.message);
            onDelete();
            onClose();
        } else {
            if(response.error) {
                message.error(response.error.message)
            }
            message.error("Unknown error")
        }
    }

    const handleCancelClick = () => {
        setIsEditing(false);
        setEditingExercise(deepCopy(savedExercise));
    };

    function handleCorrectAnswerChange(id: number) {
        if (editingExercise) {
            setEditingExercise({
                ...editingExercise,
                correctAnswerIndex: id
            });
        }
    }

    const handleAddAnswer = () => {
        if (editingExercise) {
            const possibleAnswers: string[] = editingExercise.possibleAnswers;
            setEditingExercise({
                ...editingExercise,
                possibleAnswers: [...possibleAnswers, '']
            });
        }
    };

    const handleUpdateAnswer = (newText: string, index: number) => {
        if (editingExercise) {
            const newPossibleAnswers = editingExercise.possibleAnswers;
            newPossibleAnswers[index] = newText
            setEditingExercise({
                ...editingExercise,
                possibleAnswers: newPossibleAnswers
            });
        }
    };

    function handleDeleteAnswer(index: number) {
        if (editingExercise) {
            const newPossibleAnswers = editingExercise.possibleAnswers;
            newPossibleAnswers.splice(index, 1)
            setEditingExercise({
                ...editingExercise,
                possibleAnswers: newPossibleAnswers,
                correctAnswerIndex: editingExercise.correctAnswerIndex === editingExercise.possibleAnswers.length ?
                    editingExercise.correctAnswerIndex - 1 : editingExercise.correctAnswerIndex
            });
        }
    }

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (exercise) {
            setEditingExercise({
                ...exercise,
                [e.target.name]: e.target.value,
            });
        }
    };

    const handleInputTextChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        console.log(e)
        if (exercise) {
            setEditingExercise({
                ...exercise,
                [e.target.name]: e.target.value
            });
        }
    };

    function handleTagsChange(tags: TagModel[]) {
        if (editingExercise) {
            setEditingExercise({
                ...editingExercise,
                tags: tags
            });
        }
    }

    return (
        <Modal
            title="Exercise"
            open={open}
            onCancel={onClose}
            footer={
                <div style={{textAlign: 'center'}}>
                    {isEditing ? (
                        <>
                            <Button type="primary" onClick={handleSaveClick} style={{marginRight: '10px'}}>
                                Save <SaveOutlined/>
                            </Button>
                            <Button onClick={handleCancelClick}>
                                Cancel <CloseOutlined/>
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button type="primary" onClick={handleEditClick} style={{marginRight: '10px'}}>
                                Edit <EditOutlined/>
                            </Button>
                            <Button onClick={handleDeleteClick} danger>
                                Delete <DeleteOutlined/>
                            </Button>
                        </>
                    )}
                </div>
            }
        >
            {editingExercise ? (
                <Card title={
                    isEditing ? (
                        <Input
                            name="title"
                            value={editingExercise.title}
                            onChange={handleInputChange}
                            style={{ fontWeight: 'bold', width: '100%' }}
                        />
                    ) : (
                        editingExercise.title
                    )
                }>
                    <div>
                        {isEditing ? (
                            <TagsEditableList tags={editingExercise.tags} onUpdate={handleTagsChange}/>
                        ) : (
                            <TagList tags={editingExercise.tags}/>
                        )}
                    </div>
                    <div style={{margin: '16px 8px'}}>
                        {isEditing ? (
                            <Input.TextArea
                                name="description"
                                value={editingExercise.description}
                                onChange={handleInputTextChange}
                                autoSize={{ minRows: 4, maxRows: 10 }}
                                style={{width: '100%'}}
                            />
                        ) : (
                            editingExercise.description
                        )}
                    </div>
                    <List
                        bordered
                        dataSource={editingExercise.possibleAnswers}
                        renderItem={(answer, index) => {
                            const answerLetter = getAnswerLetter(index);
                            const isCorrect = index === editingExercise.correctAnswerIndex;
                            const answerStyle = isCorrect ? {
                                backgroundColor: '#f6ffed', borderColor: '#b7eb8f'
                            } : {};

                            return (
                                <List.Item style={answerStyle}>
                                    {isEditing && (
                                        <Radio
                                            checked={isCorrect}
                                            onChange={() => handleCorrectAnswerChange(index)}
                                        />
                                    )}
                                    <span style={{marginRight: '16px'}}> {answerLetter} </span>
                                    {isEditing ? (
                                        <>
                                            <Input
                                                value={answer}
                                                onChange={(e) =>
                                                    handleUpdateAnswer(e.target.value, index)
                                                }
                                            />
                                            <Button style={{marginLeft: '16px'}}
                                                    onClick={() => handleDeleteAnswer(index)} icon={<DeleteOutlined/>}/>
                                        </>
                                    ) : (
                                        answer
                                    )}
                                </List.Item>
                            );
                        }}
                    />
                    {isEditing && (
                        <div style={{textAlign: 'center', marginTop: '16px'}}>
                            <Button onClick={handleAddAnswer}>Add Answer</Button>
                        </div>
                    )}
                </Card>
            ) : (
                <p>Loading...</p>
            )}
        </Modal>
    );
};

export default ExerciseDetailModal;
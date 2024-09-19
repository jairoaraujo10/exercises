import React, {useState} from "react";
import {Button, Card, Input, List, message, Modal, Radio} from "antd";
import {DeleteOutlined, SaveOutlined} from '@ant-design/icons';
import {FullExercise, TagModel} from "../../interfaces/Exercise.tsx";
import TagsEditableList from "../TagsEditableList";
import {CreateExercise} from "../../context/AuthContext/utils.tsx";

interface ExerciseDetailModalProps {
    open: boolean;
    onClose: () => void;
}

const getAnswerLetter = (index: number): string => {
    return String.fromCharCode(97 + index) + '.';
}

const CreateExerciseModal: React.FC<ExerciseDetailModalProps> = ({open, onClose}) => {
    const [exercise, setExercise] = useState<FullExercise>({
        id: 'tmp', title: "", description: "", tags: [], possibleAnswers: ['', ''], correctAnswerIndex: 0
    });

    async function handleSaveClick() {
        const response = await CreateExercise(exercise)
        if(response.data) {
            message.success(response.data.message);
            onClose();
        } else {
            if(response.error) {
                message.error(response.error.message)
            }
            message.error("Unknown error")
        }
    }

    function handleCorrectAnswerChange(id: number) {
        if (exercise) {
            setExercise({
                ...exercise,
                correctAnswerIndex: id
            });
        }
    }

    const handleAddAnswer = () => {
        if (exercise) {
            const possibleAnswers: string[] = exercise.possibleAnswers;
            setExercise({
                ...exercise,
                possibleAnswers: [...possibleAnswers, '']
            });
        }
    };

    const handleUpdateAnswer = (newText: string, index: number) => {
        if (exercise) {
            const newPossibleAnswers = exercise.possibleAnswers;
            newPossibleAnswers[index] = newText
            setExercise({
                ...exercise,
                possibleAnswers: newPossibleAnswers
            });
        }
    };

    function handleDeleteAnswer(index: number) {
        if (exercise) {
            const newPossibleAnswers = exercise.possibleAnswers;
            newPossibleAnswers.splice(index, 1)
            setExercise({
                ...exercise,
                possibleAnswers: newPossibleAnswers
            });
        }
    }

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (exercise) {
            setExercise({
                ...exercise,
                [e.target.name]: e.target.value,
            });
        }
    };

    const handleInputTextChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        console.log(e)
        if (exercise) {
            setExercise({
                ...exercise,
                [e.target.name]: e.target.value
            });
        }
    };

    function handleTagsChange(tags: TagModel[]) {
        if (exercise) {
            setExercise({
                ...exercise,
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
                    <Button type="primary" onClick={handleSaveClick} style={{marginRight: '10px'}}>
                        Save <SaveOutlined/>
                    </Button>
                </div>
            }
        >
            <Card title={
                <Input
                    name="title"
                    value={exercise.title}
                    onChange={handleInputChange}
                    style={{ width: '100%' }}
                />
            }>
                <div>
                    <TagsEditableList tags={exercise.tags} onUpdate={handleTagsChange}/>
                </div>
                <div style={{margin: '16px 8px'}}>
                    <Input.TextArea
                        name="description"
                        value={exercise.description}
                        onChange={e=>handleInputTextChange(e)}
                        autoSize={{ minRows: 4, maxRows: 10 }}
                        style={{width: '100%'}}
                    />
                </div>
                <List
                    bordered
                    dataSource={exercise.possibleAnswers}
                    renderItem={(answer, index) => {
                        const answerLetter = getAnswerLetter(index);
                        const isCorrect = index === exercise.correctAnswerIndex;
                        const answerStyle = isCorrect ? {
                            backgroundColor: '#f6ffed', borderColor: '#b7eb8f'
                        } : {};

                        return (
                            <List.Item style={answerStyle}>
                                <Radio
                                    checked={isCorrect}
                                    onChange={() => handleCorrectAnswerChange(index)}
                                />
                                <span style={{marginRight: '16px'}}> {answerLetter} </span>
                                <Input
                                    value={answer}
                                    onChange={(e) => handleUpdateAnswer(e.target.value, index)}
                                />
                                <Button style={{marginLeft: '16px'}} onClick={() => handleDeleteAnswer(index)} icon={<DeleteOutlined/>}/>
                            </List.Item>
                        );
                    }}
                />
                <div style={{textAlign: 'center', marginTop: '16px'}}>
                    <Button onClick={handleAddAnswer}>Add Answer</Button>
                </div>
            </Card>
        </Modal>
    );
};

export default CreateExerciseModal;
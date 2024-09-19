import React, {useState, useEffect, useCallback} from 'react';
import {Button, Input, Layout, Select, Table, TableColumnsType} from 'antd';
import {GetExercise, SearchExercisesRequest} from "../../context/AuthContext/utils.tsx";
import {PlusOutlined} from '@ant-design/icons';
import {BasicExercise, FullExercise, SearchResponse, TagModel} from "../../interfaces/Exercise.tsx";
import TagList from "../TagList";
import ExerciseDetailModal from "../ExerciseDetailModal";
import {useLocation, useNavigate} from "react-router-dom";
import CreateExerciseModal from "../CreateExerciseModal";
import {Option} from "antd/lib/mentions";
import {Content} from "antd/es/layout/layout";

const ExerciseList: React.FC = () => {
    const [searchResponse, setSearchResponse] = useState<SearchResponse>({
        exercises: [],
        total: 0,
    });
    const [loading, setLoading] = useState(false);
    const [searchTerm, setSearchTerm] = useState<string>("");
    const [selectedExercise, setSelectedExercise] = useState<FullExercise | null>(null);
    const [createExercise, setCreateExercise] = useState<boolean>(false);

    const [currentPage, setCurrentPage] = useState<number>(1);
    const pageSize = 20;
    const navigate = useNavigate();
    const location = useLocation();

    const fetchExercises = useCallback( async () => {
        try {
            setLoading(true)
            const response = await SearchExercisesRequest(pageSize, currentPage, searchTerm, []);
            if (response && response.status === 200)
                setSearchResponse(response.data);
        } catch (error) {
            console.error('Error fetching exercises:', error);
        } finally {
            setLoading(false)
        }
    }, [pageSize, currentPage, searchTerm]);

    useEffect(() => {
        fetchExercises();
    }, [fetchExercises]);

    const fetchFullExercise = useCallback(async (pathname: string) => {
        if(pathname.startsWith('/new-exercise')) {
            setCreateExercise(true)
        } else {
            const exerciseId = pathname.split('/exercises/')[1];
            if (exerciseId && searchResponse.exercises.find(ex => ex.id === exerciseId)) {
                const response = await GetExercise(exerciseId);
                if (response && response.status === 200) {
                    setSelectedExercise(response.data);
                } else {
                    navigate(`/exercises`)
                }
            }
        }
    }, [navigate, searchResponse.exercises]);

    useEffect(() => {
        fetchFullExercise(location.pathname);
    }, [fetchFullExercise, location.pathname]);

    const handleCreateExercise = () => {
        setCreateExercise(true);
        navigate('/new-exercise');
    }

    const handleTitleClick = async (exercise: BasicExercise) => {
        const response = await GetExercise(exercise.id);
        if (response && response.status === 200) {
            setSelectedExercise(response.data);
            navigate(`/exercises/${exercise.id}`);
        }
    };

    const handleSearch = (value: string) => {
        setSearchTerm(value);
        setCurrentPage(1);
    };

    const handleChangePage = (page: number) => {
        setCurrentPage(page);
    };

    const deleteExercise = () => {
        if(selectedExercise) {
            setSearchResponse({
                exercises: searchResponse.exercises.filter(exercise => exercise.id !== selectedExercise.id),
                total: searchResponse.total - 1,
            });
        }
    };

    const handleCloseModal = () => {
        setSelectedExercise(null);
        setCreateExercise(false);
        navigate(`/exercises`);
    };

    const columns: TableColumnsType<BasicExercise> = [
        {
            title: 'Title',
            dataIndex: 'title',
            key: 'title',
            render: (title: string, record: BasicExercise) => (
                <a onClick={() => handleTitleClick(record)}>{title}</a>
            ),
        },
        {
            title: 'Tags',
            dataIndex: 'tags',
            key: 'tags',
            render: (tags: TagModel[]) => <TagList tags={tags} />,
        },
    ];

    return (
        <Layout className="site-layout">
            <Content style={{ margin: '0 16px' }}>
                <div style={{margin: '16px 0'}}>
                    <Input.Search
                        placeholder="Search by term"
                        enterButton="Search"
                        onSearch={handleSearch}
                        style={{ width: 400 }}
                    />
                    <Select
                        mode="multiple"
                        allowClear
                        placeholder="Filter by tags"
                        // onChange={handleTagChange}
                        style={{width: '200px', marginRight: '8px'}}
                    >
                        <Option key="tag1" value="tag1">Tag 1</Option>
                        <Option key="tag2" value="tag2">Tag 2</Option>
                        <Option key="tag3" value="tag3">Tag 3</Option>
                    </Select>
                    <Button type="primary" icon={<PlusOutlined/>} style={{ float: 'right' }} onClick={() => handleCreateExercise()}>
                        Add Exercise
                    </Button>
                </div>
            <Table
                rowKey={(exercise) => exercise.id}
                columns={columns}
                dataSource={searchResponse.exercises}
                pagination={{
                    current: currentPage,
                    pageSize: pageSize,
                    total: searchResponse.total,
                    onChange: handleChangePage,
                }}
                loading={loading}
            />
            {selectedExercise && (
                <ExerciseDetailModal
                    open={!!selectedExercise}
                    onDelete={deleteExercise}
                    onClose={handleCloseModal}
                    exercise={selectedExercise}
                />
            )}
            {createExercise && (
                <CreateExerciseModal
                    open={createExercise}
                    onClose={handleCloseModal}
                />
            )}
            </Content>
        </Layout>
    );
};

export default ExerciseList;
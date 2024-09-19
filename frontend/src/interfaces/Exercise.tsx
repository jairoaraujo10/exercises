export interface TagModel {
    value: string;
}

export interface BasicExercise {
    id: string;
    title: string;
    tags: TagModel[];
}

export interface SearchResponse {
    exercises: BasicExercise[];
    total: number;
}

export interface FullExercise {
    id: string;
    title: string;
    tags: TagModel[];
    description: string;
    possibleAnswers: string[];
    correctAnswerIndex: number;
}

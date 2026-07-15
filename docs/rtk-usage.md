# Redux Toolkit (RTK) Usage Guide

Redux Toolkit (RTK) is the official, opinionated, batteries-included toolset for efficient Redux development. It was created to address three common concerns about Redux:
1. "Configuring a Redux store is too complicated."
2. "I have to add a lot of packages to get Redux to do anything useful."
3. "Redux requires too much boilerplate code."

---

## 1. What RTK Does

RTK provides standard utilities that simplify common Redux use cases. It includes:

*   **`configureStore()`**: Wraps standard Redux `createStore` to provide simplified configuration options and good defaults. It automatically combines your slice reducers, adds whatever Redux middleware you supply (including `redux-thunk` by default), and enables the Redux DevTools Extension.
*   **`createSlice()`**: Accepts an object of reducer functions, a slice name, and an initial state value, and automatically generates a slice reducer with corresponding action creators and action types. **It uses Immer internally**, allowing you to write "mutating" logic that is safely turned into immutable updates.
*   **`createAsyncThunk()`**: Accepts an action type string and a function that returns a promise, and generates a thunk that dispatches `pending/fulfilled/rejected` action types based on that promise.
*   **`createEntityAdapter()`**: Generates a set of reusable reducers and selectors to manage normalized data in the store.
*   **RTK Query**: An advanced data fetching and caching tool built on top of RTK, designed to eliminate the need to hand-write data fetching logic.

---

## 2. Installation

You can install RTK along with `react-redux` using your preferred package manager:

### Using npm
```bash
npm install @reduxjs/toolkit react-redux
```

### Using yarn
```bash
yarn add @reduxjs/toolkit react-redux
```

### Using pnpm
```bash
pnpm add @reduxjs/toolkit react-redux
```

### For TypeScript Projects
RTK is written in TypeScript and has built-in type definitions, so no extra `@types` packages are needed.

---

## 3. Core Concepts & Code Examples

Here is a step-by-step example of setting up a simple **Counter** state using RTK in a React application.

### Step 1: Create a Redux Slice
A "slice" contains the reducer logic and actions for a single feature.

```typescript
// features/counter/counterSlice.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface CounterState {
  value: number;
}

const initialState: CounterState = {
  value: 0,
};

export const counterSlice = createSlice({
  name: 'counter',
  initialState,
  reducers: {
    increment: (state) => {
      // Immer lets us write "mutating" logic safely
      state.value += 1;
    },
    decrement: (state) => {
      state.value -= 1;
    },
    incrementByAmount: (state, action: PayloadAction<number>) => {
      state.value += action.payload;
    },
  },
});

// Export actions for use in components
export const { increment, decrement, incrementByAmount } = counterSlice.actions;

// Export reducer for store configuration
export default counterSlice.reducer;
```

### Step 2: Configure the Store
Combine your reducers and configure the store.

```typescript
// app/store.ts
import { configureStore } from '@reduxjs/toolkit';
import counterReducer from '../features/counter/counterSlice';

export const store = configureStore({
  reducer: {
    counter: counterReducer,
  },
});

// Infer the `RootState` and `AppDispatch` types from the store itself
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
```

### Step 3: Provide the Store to React
Wrap your React application with the `Provider` component from `react-redux`.

```tsx
// main.tsx or index.tsx
import React from 'react';
import ReactDOM from 'react-dom/client';
import { Provider } from 'react-redux';
import { store } from './app/store';
import App from './App';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <Provider store={store}>
      <App />
    </Provider>
  </React.StrictMode>
);
```

### Step 4: Use Redux State and Actions in Components
Use React-Redux hooks to interact with the store.

```tsx
// App.tsx
import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState } from './app/store';
import { increment, decrement, incrementByAmount } from './features/counter/counterSlice';

export default function App() {
  const count = useSelector((state: RootState) => state.counter.value);
  const dispatch = useDispatch();

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
      <h1>Counter: {count}</h1>
      <button onClick={() => dispatch(increment())}>Increment</button>
      <button onClick={() => dispatch(decrement())} style={{ margin: '0 10px' }}>Decrement</button>
      <button onClick={() => dispatch(incrementByAmount(5))}>Increment by 5</button>
    </div>
  );
}
```

---

## 4. RTK Query (Data Fetching)

RTK Query is an optional addon included in the `@reduxjs/toolkit` package. It simplifies fetching and caching server state.

### Defining an API Service:
```typescript
// services/pokemon.ts
import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

export const pokemonApi = createApi({
  reducerPath: 'pokemonApi',
  baseQuery: fetchBaseQuery({ baseUrl: 'https://pokeapi.co/api/v2/' }),
  endpoints: (builder) => ({
    getPokemonByName: builder.query<any, string>({
      query: (name) => `pokemon/${name}`,
    }),
  }),
});

// Auto-generated hooks based on defined endpoints
export const { useGetPokemonByNameQuery } = pokemonApi;
```

### Adding to Store:
```typescript
// app/store.ts
import { configureStore } from '@reduxjs/toolkit';
import { pokemonApi } from '../services/pokemon';

export const store = configureStore({
  reducer: {
    [pokemonApi.reducerPath]: pokemonApi.reducer,
  },
  // Adding the api middleware enables caching, invalidation, polling, and other features
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(pokemonApi.middleware),
});
```

### Using in a Component:
```tsx
import React from 'react';
import { useGetPokemonByNameQuery } from './services/pokemon';

export default function Pokemon() {
  const { data, error, isLoading } = useGetPokemonByNameQuery('bulbasaur');

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error occurred!</div>;

  return (
    <div>
      <h3>{data.species.name}</h3>
      <img src={data.sprites.front_default} alt={data.species.name} />
    </div>
  );
}
```

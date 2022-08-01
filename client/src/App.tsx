import { useEffect, useRef } from "react";
import { useQuery } from "react-query";
import { fetchWeight } from "./api/withings";
import "./App.css";
import { Spinner } from "./components/Spinner";

function App() {
  const { isLoading, isError, data, refetch } = useQuery("weight", () => fetchWeight());
  const refetchCalled = useRef(false);

  useEffect(() => {
    if (!refetchCalled.current) {
      refetch();
      refetchCalled.current = true;
    }
  }, []);

  if (isLoading) {
    return <Spinner />;
  }

  if (isError) {
    return <>{"Loading weight was not succesful"}</>;
  }

  return <div className="App">{data?.weight ?? '?'}</div>;
}

export default App;

import { useQuery } from "react-query";
import { fetchWeight } from "./api/withings";
import "./App.css";
import { Spinner } from "./components/Spinner";

function App() {
  const { isLoading, isError, data } = useQuery("weight", () => fetchWeight());

  if (isLoading) {
    return <Spinner />;
  }

  if (isError) {
    return <>{"Loading weight was not succesful"}</>;
  }

  return <div className="App">{data?.weight ?? '?'}</div>;
}

export default App;

import { useQuery } from "react-query";
import "./App.css";
import { Spinner } from "./Spinner";

function App() {
  const { isLoading, isError, isIdle, data } = useQuery("weight", () =>
    fetch("/api/withings/measure").then((res) => res.json())
  );

  if (isIdle) {
    return null;
  }

  if (isLoading) {
    return <Spinner />;
  }

  if (isError) {
    return <>{"Loading weight was not succesful"}</>;
  }

  return <div className="App">{data}</div>;
}

export default App;

import { useQuery } from "react-query";
import { fetchWeight } from "./api/withings";
import "./App.css";
import { Spinner } from "./components/Spinner";

function App() {
  const { isLoading, isError, data: weight } = useQuery("weight", () => fetchWeight());

  if (isLoading) {
    return <Spinner />;
  }

  if (isError) {
    return <>{"Loading weight was not succesful"}</>;
  }

  return <div className="App">{weight}</div>;
}

export default App;

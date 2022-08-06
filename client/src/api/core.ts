export async function fetchJSON<T>(
  input: RequestInfo,
  init?: RequestInit | undefined
): Promise<T> {
  const headers = new Headers(init?.headers);

  headers.append("Accept", "application/json");
  headers.append("Content-Type", "application/json");

  const response = await fetch(input, { ...init, headers });
  if (!response.ok) {
    if (response.status === 401) {
      const json = await response.json();
      debugger;
      window.location.href = json._links.oauth2Login.href;
    }
    throw new Error(await response.text());
  }

  return await response.json();
}

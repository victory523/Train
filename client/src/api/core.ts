export async function fetchJSON<T>(input: RequestInfo, init?: RequestInit | undefined): Promise<T> {
    const response = await fetch(input, init);
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
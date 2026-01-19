const BASE_URL = location.origin;
const API_URL = `${BASE_URL}/api`;

const SHARE_TOKENS_LS_KEY = "share-tokens";

const fileItemTemplateElement = document.querySelector("#selected-file-item-template");

function createSelectedFileItemNode(file) {
    const item = fileItemTemplateElement.content.cloneNode(true);

    item.querySelector(".file-name").textContent = file.name;
    item.querySelector(".file-size").textContent = `${file.size / 1000} kB`

    return item;
}

const fileInputElement = document.querySelector("#file-picker");
const fileListElement = document.querySelector("#selected-file-list");

fileInputElement.addEventListener("change", () => {
    fileListElement.innerHTML = "";
    for (const file of fileInputElement.files) {
        const item = createSelectedFileItemNode(file);
        fileListElement.appendChild(item);
    }
})

const slugInputElement = document.querySelector("#slug");
const expirationInputElement = document.querySelector("#expiration-date");
const uploadButtonElement = document.querySelector("#upload-button");

function disableFormInput() {
    fileInputElement.disabled = true;
    slugInputElement.disabled = true;
    expirationInputElement.disabled = true;
    uploadButtonElement.disabled = true;
}

function getFormValues() {
    const trimmedSlugInput = slugInputElement.value.trim();

    const slug = trimmedSlugInput !== "" ? trimmedSlugInput : null;
    const hours = parseInt(expirationInputElement.value);

    const expiration = new Date(Date.now() + hours * 60 * 60 * 1000);

    return {slug, expiration};
}

async function createShare(sharePartial) {
    const shareCreateEndpoint = `${API_URL}/shares`;

    const {slug, expiration} = sharePartial;

    const response = await fetch(shareCreateEndpoint, {
        method: "POST", headers: {
            "Content-Type": "application/json",
        }, body: JSON.stringify({
            slug, expiresAt: expiration.toISOString(),
        })
    })
    const body = await response.json();

    if (response.ok) {
        return body;
    }

    throw new Error(body.message);
}

function enableFormInput() {
    fileInputElement.disabled = false;
    slugInputElement.disabled = false;
    expirationInputElement.disabled = false;
    uploadButtonElement.disabled = false;
}

function saveToken(shareId, token) {
    const shareTokens = JSON.parse(localStorage.getItem(SHARE_TOKENS_LS_KEY)) || [];
    shareTokens.push({id: shareId, token})
    localStorage.setItem(SHARE_TOKENS_LS_KEY, JSON.stringify(shareTokens));
}

async function uploadAttachment(shareId, token, file) {
    const shareCreateEndpoint = `${API_URL}/shares/${shareId}/attachments`;
    const formData = new FormData();

    formData.append("file", file);

    const response = await fetch(shareCreateEndpoint, {
        method: "POST", headers: {
            "Management-Token": token,
        }, body: formData,
    })

    return await response.json();
}

async function uploadAttachments(shareId, token, files) {
    for (const file of files) {
        await uploadAttachment(shareId, token, file);
    }
}

async function finalizeShare(shareId, token) {
    const patchShareEndpoint = `${API_URL}/shares/${shareId}`;

    const response = await fetch(patchShareEndpoint, {
        method: "PATCH", headers: {
            "content-type": "application/json", "Management-Token": token,
        }, body: JSON.stringify({
            isOpen: false,
        }),
    })

    if (response.ok) {
        return;
    }

    const body = await response.json();
    console.error(body);
}

const fileUploadFormElement = document.querySelector("#file-upload-form");
const errorOutputElement = document.querySelector("#error-output");

fileUploadFormElement.addEventListener("submit", async event => {
    event.preventDefault();

    errorOutputElement.textContent = "";
    disableFormInput();

    const formValues = getFormValues();
    let createdShare;

    try {
        createdShare = await createShare(formValues);
    } catch (e) {
        errorOutputElement.textContent = e.message;
        enableFormInput();
        return;
    }

    const {share, token} = createdShare;

    slugInputElement.value = share.slug;
    saveToken(share.id, token);

    await uploadAttachments(share.id, token, fileInputElement.files);
    await finalizeShare(share.id, token);

    location.href = "uploads";
    enableFormInput();
})